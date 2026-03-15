#!/usr/bin/env bash
# Generates a local CA, server cert, and a sample client cert for development.
# Do NOT use these in production — get real certs from your PKI / Let's Encrypt.

set -euo pipefail
CERT_DIR="$(dirname "$0")/nginx/certs"
mkdir -p "$CERT_DIR"

echo "── Generating CA key & cert ─────────────────────────────────────────────"
openssl genrsa -out "$CERT_DIR/ca.key" 4096
openssl req -new -x509 -days 3650 -key "$CERT_DIR/ca.key" \
  -out "$CERT_DIR/ca.crt" \
  -subj "/CN=Dev CA/O=Local/C=US"

echo "── Generating server key & CSR ─────────────────────────────────────────"
openssl genrsa -out "$CERT_DIR/server.key" 2048
openssl req -new -key "$CERT_DIR/server.key" \
  -out "$CERT_DIR/server.csr" \
  -subj "/CN=localhost/O=Local/C=US"

# SAN extension so browsers / curl don't complain
cat > "$CERT_DIR/server_ext.cnf" <<EOF
[SAN]
subjectAltName=DNS:localhost,IP:127.0.0.1
EOF

openssl x509 -req -days 825 \
  -in "$CERT_DIR/server.csr" \
  -CA "$CERT_DIR/ca.crt" -CAkey "$CERT_DIR/ca.key" -CAcreateserial \
  -out "$CERT_DIR/server.crt" \
  -extfile "$CERT_DIR/server_ext.cnf" -extensions SAN

echo "── Generating client key & cert (signed by same CA) ────────────────────"
openssl genrsa -out "$CERT_DIR/client.key" 2048
openssl req -new -key "$CERT_DIR/client.key" \
  -out "$CERT_DIR/client.csr" \
  -subj "/CN=dev-client/O=Local/C=US"

openssl x509 -req -days 825 \
  -in "$CERT_DIR/client.csr" \
  -CA "$CERT_DIR/ca.crt" -CAkey "$CERT_DIR/ca.key" -CAcreateserial \
  -out "$CERT_DIR/client.crt"

# Clean up intermediates
rm -f "$CERT_DIR"/*.csr "$CERT_DIR"/*.cnf "$CERT_DIR"/*.srl

echo ""
echo "✅  Certs written to $CERT_DIR"
echo ""
echo "Test with:"
echo "  curl -k --cert $CERT_DIR/client.crt --key $CERT_DIR/client.key https://localhost"
