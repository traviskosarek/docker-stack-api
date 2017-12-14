/*
 * Copyright 2016 the original author or authors.
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
package docker.api;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import docker.api.container.Container;
import docker.api.node.Node;
import docker.api.service.Service;
import docker.api.swarm.Swarm;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DockerApiControllerTests
{
    @Test
    public void noParamGreetingShouldReturnDefaultMessage() throws Exception
    {
        DockerApiController dockerApiController = spy(DockerApiController.class);
        doReturn(new Swarm("test")).when(dockerApiController).getSwarm();
        doReturn(new ArrayList<Node>()).when(dockerApiController).getNodes();
        doReturn(new ArrayList<Service>()).when(dockerApiController).getServices(Mockito.anyString());
        doReturn(new ArrayList<Container>()).when(dockerApiController).getContainers(Mockito.anyString());

        ResponseEntity<Swarm> swarm = dockerApiController.swarm();

        assert(swarm.getStatusCode().is2xxSuccessful());
    }

    // @Test
    // public void paramGreetingShouldReturnTailoredMessage() throws Exception {

    //     this.mockMvc.perform(get("/greeting").param("name", "Spring Community"))
    //             .andDo(print()).andExpect(status().isOk())
    //             .andExpect(jsonPath("$.content").value("Hello, Spring Community!"));
    // }

}
