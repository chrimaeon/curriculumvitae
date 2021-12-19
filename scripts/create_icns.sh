#!/usr/bin/env sh

#
# Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

while [ $# -gt 0 ]; do
    key="$1"

    case $key in
        -o|--out_dir)
            OUTPUT_DIR="$2"
            shift
            shift
            ;;
        *)
            INPUT_FILE_PATH="$1"
            shift
            ;;
    esac
done

if [ ! -f "$INPUT_FILE_PATH" ]
then
    echo "file not found: $INPUT_FILE_PATH"
    exit 1
fi

if [ -z "$OUTPUT_DIR" ]
then
  OUTPUT_DIR=$PWD
fi

CV_TEMP_DIR=${TMPDIR}CurriculimVitae/

OUTPUT_ICON_SET_DIR=${CV_TEMP_DIR}CV.iconset

mkdir -p "$OUTPUT_ICON_SET_DIR"

sips -z 16 16    "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_16x16.png"
sips -z 32 32    "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_16x16@2x.png"
sips -z 32 32    "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_32x32.png"
sips -z 64 64    "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_32x32@2x.png"
sips -z 128 128  "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_128x128.png"
sips -z 256 256  "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_128x128@2x.png"
sips -z 256 256  "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_256x256.png"
sips -z 512 512  "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_256x256@2x.png"
sips -z 512 512  "$INPUT_FILE_PATH" --out "${OUTPUT_ICON_SET_DIR}/icon_512x512.png"

iconutil -c icns "$OUTPUT_ICON_SET_DIR"

cp "${CV_TEMP_DIR}/CV.icns" "$OUTPUT_DIR"

rm -r "$CV_TEMP_DIR"
