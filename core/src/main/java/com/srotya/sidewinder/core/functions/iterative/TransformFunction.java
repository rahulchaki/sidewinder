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
package com.srotya.sidewinder.core.functions.iterative;

import com.srotya.sidewinder.core.storage.DataPoint;
import com.srotya.sidewinder.core.storage.DataPointIterator;

public abstract class TransformFunction extends FunctionIterator {

	public TransformFunction(DataPointIterator iterator, boolean isFp) {
		super(iterator, isFp);
	}

	@Override
	public DataPoint next() {
		DataPoint next = iterator.next();
		if (isFp) {
			next.setLongValue(Double.doubleToLongBits(transform(Double.longBitsToDouble(next.getLongValue()))));
		} else {
			next.setLongValue(transform(next.getLongValue()));
		}
		return null;
	}

	protected abstract double transform(double value);

	protected abstract long transform(long value);
	
	@Override
	public int getNumberOfArgs() {
		return 0;
	}
	
	@Override
	public void init(Object[] args) throws Exception {
	}
}