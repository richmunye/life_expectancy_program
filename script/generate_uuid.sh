#!/bin/bash

generate_uuid() {
  uuidgen
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  generate_uuid
fi
