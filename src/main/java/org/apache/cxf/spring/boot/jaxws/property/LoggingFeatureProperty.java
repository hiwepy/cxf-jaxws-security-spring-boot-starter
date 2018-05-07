/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.cxf.spring.boot.jaxws.property;

import org.apache.cxf.ext.logging.AbstractLoggingInterceptor;

public class LoggingFeatureProperty {

	private int limit = AbstractLoggingInterceptor.DEFAULT_LIMIT;
	private long threshold = AbstractLoggingInterceptor.DEFAULT_THRESHOLD;
	/**
	 * Whether Log binary content
	 */
	private boolean logBinary;
	/**
	 * Whether Log multipart content, defaults to true
	 */
	private boolean logMultipart = true;
	/**
	 * Whether pretty Log content
	 */
	private boolean prettyLogging;
	/**
	 * Whether Log verbose
	 */
	private boolean verbose;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public boolean isLogBinary() {
		return logBinary;
	}

	public void setLogBinary(boolean logBinary) {
		this.logBinary = logBinary;
	}

	public boolean isLogMultipart() {
		return logMultipart;
	}

	public void setLogMultipart(boolean logMultipart) {
		this.logMultipart = logMultipart;
	}

	public boolean isPrettyLogging() {
		return prettyLogging;
	}

	public void setPrettyLogging(boolean prettyLogging) {
		this.prettyLogging = prettyLogging;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
}
