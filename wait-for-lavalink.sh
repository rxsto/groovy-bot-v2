#!/bin/sh
set -e

cmd="$@"

until curl http://localhost:8080; do
  >&2 echo "[Core] Lavalink isn't running!"
  sleep 1
 done
echo "[Core] Starting ..."

exec ${cmd}