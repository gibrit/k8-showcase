/*
 * Copyright 2021 Saltik Bugra Avci ben@saltuk.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.saltuk.core.stream;

import io.vertx.core.json.JsonObject;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.saltuk.core.StringUtils;
import org.saltuk.core.json.JsonDataBase;

/**
 * Simple Stream Token
 *
 * @author saltuk
 */
class StreamTokenImpl extends JsonDataBase implements StreamToken {

    private String token;
    private String adddress;
    private String user;

    public StreamTokenImpl(String user) {
        this.adddress = StringUtils.append("api.live.", DigestUtils.md5Hex(UUID.randomUUID().toString()));
        this.token = DigestUtils.md5Hex(UUID.randomUUID().toString());
        this.user = user;
    }

    public StreamTokenImpl() {
    }

    @Override
    public JsonObject asJson() {
        return new JsonObject().put("token", this.token).put("address", this.adddress).put("user", user);
    }

    @Override
    public void fromJson(JsonObject value) {
        this.token = value.getString("token");
        this.adddress = value.getString("address");
        this.user = value.getString("user");

    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public String address() {
        return this.adddress;
    }

    @Override
    public String user() {
        return this.user;
    }

    @Override
    public String addressStatistics() {
        return StringUtils.append(this.adddress, ".statistics");
    }

    @Override
    public String tokenAddress(String name) {
        return StringUtils.append(name, ".tokens.", this.user != null ? this.user : "public");
    }

    @Override
    public JsonObject asResult() {
        return new JsonObject().put("address", this.adddress).put("token", token).put("address_statistics", this.adddress);
    }

}
