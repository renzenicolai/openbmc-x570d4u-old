#!/bin/bash
# Copyright 2023 Google LLC
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

systemctl stop gbmc-br-dhcp

# in some cases dhcp-done might be run already, in this case we want
# a powercycle for a clean install
systemctl is-active -q dhcp-done@* && exit 1

# stop dhcp term service to prevent race condition
systemctl is-active --quiet gbmc-br-dhcp-term && systemctl stop gbmc-br-dhcp-term

# start the dhcp service
systemctl start gbmc-br-dhcp
