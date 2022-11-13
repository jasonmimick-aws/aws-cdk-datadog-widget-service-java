#!/usr/bin/env bash

set -x
aws logs describe-log-groups | jq --arg log "${1}" '.logGroups[] |
    select(.logGroupName|contains($log)) | .logGroupName' | xargs -I {} aws logs tail {}

