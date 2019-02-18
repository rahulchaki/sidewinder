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
package com.srotya.sidewinder.core.storage.archival;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.srotya.sidewinder.core.storage.ArchiveException;
import com.srotya.sidewinder.core.storage.Buffer;
import com.srotya.sidewinder.core.storage.ByteString;
import com.srotya.sidewinder.core.storage.compression.Writer;

/**
 * @author ambud
 */
public interface Archiver {

	public void init(Map<String, String> conf) throws IOException;

	public void archive(TimeSeriesArchivalObject archivalObject) throws ArchiveException;

	public List<TimeSeriesArchivalObject> unarchive() throws ArchiveException;

	public static void serializeToStream(DataOutputStream bos, TimeSeriesArchivalObject blob) throws IOException {
		bos.writeUTF(blob.getDb());
		bos.writeUTF(blob.getMeasurement());
		bos.writeUTF(blob.getSeriesKey().toString());
		bos.writeInt(blob.getTsBucket());
		bos.writeInt(blob.getData().length);
		bos.write(blob.getData());
		bos.flush();
	}

	public static TimeSeriesArchivalObject deserializeFromStream(DataInputStream bis) throws IOException {
		TimeSeriesArchivalObject bucketWraper = new TimeSeriesArchivalObject();
		bucketWraper.setDb(bis.readUTF());
		bucketWraper.setMeasurement(bis.readUTF());
		bucketWraper.setSeriesKey(new ByteString(bis.readUTF()));
		bucketWraper.setTsBucket(bis.readInt());
		byte[] buf = new byte[bis.readInt()];
		bis.read(buf);
		bucketWraper.setData(buf);
		return bucketWraper;
	}

	public static byte[] writerToByteArray(Writer writer) {
		Buffer rawBytes = writer.getRawBytes();
		int limit = rawBytes.remaining();
		byte[] buf = new byte[limit];
		rawBytes.get(buf);
		return buf;
	}
}
