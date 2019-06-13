/*
 * Copyright 2009-2011 Jon Stevens et al.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thegrizzlylabs.sardineandroid;

import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;
import com.thegrizzlylabs.sardineandroid.impl.SardineException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class AuthenticationTest {
    @Test
    public void testBasicAuth() throws Exception {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials("jenkins", "jenkins");
        try {
            URI url = URI.create("http://test.cyberduck.ch/dav/basic/");
            final List<DavResource> resources = sardine.list(url.toString());
            assertNotNull(resources);
            assertFalse(resources.isEmpty());
        } catch (SardineException e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testDigestAuth() throws Exception {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials("jenkins", "jenkins");
        try {
            URI url = URI.create("http://test.cyberduck.ch/dav/digest/");
            final List<DavResource> resources = sardine.list(url.toString());
            assertNotNull(resources);
            assertFalse(resources.isEmpty());
        } catch (SardineException e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testDigestAuthWithBasicPreemptiveAuthenticationEnabled() throws Exception {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials("jenkins", "jenkins");
        URI url = URI.create("http://test.cyberduck.ch/dav/digest/");
        sardine.enablePreemptiveAuthentication(url.getHost());
        assertNotNull(sardine.list(url.toString()));
    }

    @Ignore
    @Test
    public void testBasicPreemptiveAuth() throws Exception {
//		final HttpClientBuilder client = HttpClientBuilder.create();
//		final CountDownLatch count = new CountDownLatch(1);
//		client.setDefaultCredentialsProvider(new BasicCredentialsProvider()
//		{
//			@Override
//			public Credentials getCredentials(AuthScope authscope)
//			{
//				// Set flag that credentials have been used indicating preemptive authentication
//				count.countDown();
//				return new Credentials()
//				{
//					public Principal getUserPrincipal()
//					{
//						return new BasicUserPrincipal("anonymous");
//					}
//
//					public String getPassword()
//					{
//						return "invalid";
//					}
//				};
//			}
//		});
//		SardineImpl sardine = new SardineImpl(client);
//		URI url = URI.create("http://test.cyberduck.ch/dav/basic/");
//		//Send basic authentication header in initial request
//		sardine.enablePreemptiveAuthentication(url.getHost());
//		try
//		{
//			sardine.list(url.toString());
//			fail("Expected authorization failure");
//		}
//		catch (SardineException e)
//		{
//			// Expect Authorization Failed
//			assertEquals(401, e.getStatusCode());
//			// Make sure credentials have been queried
//			assertEquals("No preemptive authentication attempt", 0, count.getCount());
//		}
    }
}
