/**
 * Copyright Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.sidewinder.core.storage.mem;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;

import com.srotya.sidewinder.core.storage.Measurement;
import com.srotya.sidewinder.core.storage.StorageEngine;
import com.srotya.sidewinder.core.storage.disk.DiskStorageEngine;
import com.srotya.sidewinder.core.utils.BackgrounThreadFactory;
import com.srotya.sidewinder.core.utils.MiscUtils;

/**
 * @author ambud
 */
public class TestSetTagIndex {

	private static StorageEngine engine;

	@BeforeClass
	public static void before() throws IOException {
		engine = new MemStorageEngine();
		engine.configure(new HashMap<>(), Executors.newScheduledThreadPool(1));
	}

	// @Test
	public void testTagIndexPerformance() throws IOException, InterruptedException {
		MiscUtils.delete(new File("target/perf/index-dir"));
		MiscUtils.delete(new File("target/perf/data-dir"));
		DiskStorageEngine engine = new DiskStorageEngine();
		HashMap<String, String> conf = new HashMap<>();
		conf.put("index.dir", "target/perf/index-dir");
		conf.put("data.dir", "target/perf/data-dir");
		engine.configure(conf, Executors.newScheduledThreadPool(1, new BackgrounThreadFactory("bgt")));
		final long ms = System.currentTimeMillis();
		ExecutorService es = Executors.newCachedThreadPool();
		for (int k = 0; k < 6; k++) {
			es.submit(() -> {
				for (int i = 0; i < 200_000_000; i++) {
					try {
//						engine.getOrCreateTimeSeries("db1", "m1", "v10",
//								Arrays.asList(String.valueOf(i % 10_000), "test=" + "asdasdasd" + String.valueOf(i % 5),
//										"test2=" + String.valueOf(i % 5), "goliath=" + String.valueOf(i % 100_000),
//										"goliath2=" + String.valueOf(i % 1_500)),
//								4096, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (i % 1_000_000 == 0) {
						System.out.println(i + "  " + (System.currentTimeMillis() - ms) / 1000);
					}
				}
			});
		}
		es.shutdown();
		es.awaitTermination(1000, TimeUnit.SECONDS);
		System.err.println("Index time:" + (System.currentTimeMillis() - ms));
		Map<String, Map<String, Measurement>> index = engine.getMeasurementMap();
		assertEquals(1, index.size());
		Entry<String, Map<String, Measurement>> next = index.entrySet().iterator().next();
		assertEquals("db1", next.getKey());
		Entry<String, Measurement> itr = next.getValue().entrySet().iterator().next();
		assertEquals("m1", itr.getKey());
	}

}
