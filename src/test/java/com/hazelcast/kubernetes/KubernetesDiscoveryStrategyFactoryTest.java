/*
 * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.kubernetes;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.NoLogFactory;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServiceEndpointResolver.class)
public class KubernetesDiscoveryStrategyFactoryTest {

    private static final ILogger LOGGER = new NoLogFactory().getLogger("no");
    private static final String API_TOKEN = "token";

    @Mock
    DiscoveryNode discoveryNode;

    @Mock
    private DefaultKubernetesClient client;

    @Before
    public void setup()
            throws Exception {
        PowerMockito.whenNew(DefaultKubernetesClient.class).withAnyArguments().thenReturn(client);
    }

    @Test
    public void checkDiscoveryStrategyType() {
        HazelcastKubernetesDiscoveryStrategyFactory factory = new HazelcastKubernetesDiscoveryStrategyFactory();
        Class<? extends DiscoveryStrategy> strategyType = factory.getDiscoveryStrategyType();
        assertEquals(HazelcastKubernetesDiscoveryStrategy.class.getCanonicalName(), strategyType.getCanonicalName());
    }

    @Test
    public void checkProperties() {
        HazelcastKubernetesDiscoveryStrategyFactory factory = new HazelcastKubernetesDiscoveryStrategyFactory();
        Collection<PropertyDefinition> propertyDefinitions = factory.getConfigurationProperties();
        assertTrue(propertyDefinitions.contains(KubernetesProperties.SERVICE_DNS));
        assertTrue(propertyDefinitions.contains(KubernetesProperties.SERVICE_PORT));
    }

    @Test
    public void createDiscoveryStrategy() {
        HashMap<String, Comparable> properties = new HashMap<String, Comparable>();
        properties.put(KubernetesProperties.KUBERNETES_API_TOKEN.key(), API_TOKEN);
        properties.put(String.valueOf(KubernetesProperties.SERVICE_PORT), 333);
        HazelcastKubernetesDiscoveryStrategyFactory factory = new HazelcastKubernetesDiscoveryStrategyFactory();
        DiscoveryStrategy strategy = factory.newDiscoveryStrategy(discoveryNode, LOGGER, properties);
        assertTrue(strategy instanceof HazelcastKubernetesDiscoveryStrategy);
        strategy.start();
        strategy.destroy();
    }
}
