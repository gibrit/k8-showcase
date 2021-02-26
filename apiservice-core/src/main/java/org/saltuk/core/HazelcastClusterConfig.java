/*
 * Copyright 2019 saltuk.
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
package org.saltuk.core;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;

/**
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
class HazelcastClusterConfig implements ClusterConfig<Config> {

    private static ClusterConfig<Config> instance;

    public static synchronized ClusterConfig<Config> newInstance() {
        if (instance == null) {
            instance = new HazelcastClusterConfig();
        }
        return instance;
    }

    private Config config;
    private NetworkConfig network;
    private JoinConfig joinCoinfig;
    private DiscoveryConfig discoveryConfig;
    private DiscoveryStrategyConfig discoveryStrategy;

    private HazelcastClusterConfig() {
    }

    @Override
    public Config config() {
        if (this.config == null) {
            this.config = new Config();
            this.config.setProperty("hazelcast.discovery.enabled", "true");
            this.config.setNetworkConfig(this.hazelCastNetworkConfig());
        }
        return this.config;
    }

    private NetworkConfig hazelCastNetworkConfig() {
        if (this.network == null) {
            this.network = new NetworkConfig();
            this.network.setJoin(this.hazelCastJoinConfing());
        }
        return this.network;
    }

    private JoinConfig hazelCastJoinConfing() {
        if (this.joinCoinfig == null) {
            this.joinCoinfig = new JoinConfig();
            this.joinCoinfig.setMulticastConfig(new MulticastConfig().setEnabled(false));
            this.joinCoinfig.setTcpIpConfig(new TcpIpConfig().setEnabled(false));

        }
        return this.joinCoinfig;
    }

    private DiscoveryConfig hazelCastDiscovery() {
        if (discoveryConfig == null) {
            this.discoveryConfig = new DiscoveryConfig();
            this.discoveryConfig.addDiscoveryStrategyConfig(this.discoveryStrategy());
        }
        return this.discoveryConfig;

    }

    private DiscoveryStrategyConfig discoveryStrategy() {
        if (this.discoveryStrategy == null) {
            this.discoveryStrategy = new DiscoveryStrategyConfig("com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy");
            this.discoveryStrategy.addProperty("service-dns", "hazelcast-kubernetes-service");
        }
        return this.discoveryStrategy;
    }

}
