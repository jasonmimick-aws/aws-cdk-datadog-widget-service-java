#!/usr/bin/env bash

set -x
URL="${1}"
BUCKET="${2:-wed-green1-bucket}"

curl -X POST -d "{\"bucket\":\"${BUCKET}\"}" ${URL}
