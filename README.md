# Kotlin web service

Experimental Kotlin services to try out libraries for web dev

Try out -
- Javalin
- Http4K
- Ktor
- Kompose
- jooq
- JDBI (?)


Generating keys

❯ openssl genrsa -aes256 -out "private_key_encrypted.pem" -passout pass:"secret" 4096

❯ openssl rsa -in "private_key_encrypted.pem" -passin pass:"secret123" -pubout -out "public_key.pem"
