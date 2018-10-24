#!/bin/sh
set -e

cmd="$@"

until curl http://localhost:8080; do
  >&2 echo "Lavalink isn't running"
  sleep 1
 done
echo "Starting"

exec ${cmd}
