/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
/** The Logging Feature Property.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */

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

	/** Returns the limit.
	 * @return the result
	 */
	public int getLimit() {
		return limit;
	}

	/** Sets the limit.
	 * @param limit the limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/** Returns the threshold.
	 * @return the result
	 */
	public long getThreshold() {
		return threshold;
	}

	/** Sets the threshold.
	 * @param threshold the threshold
	 */
	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	/** Returns whether the log binary is enabled.
	 * @return the result
	 */
	public boolean isLogBinary() {
		return logBinary;
	}

	/** Sets the log binary.
	 * @param logBinary the logBinary
	 */
	public void setLogBinary(boolean logBinary) {
		this.logBinary = logBinary;
	}

	/** Returns whether the log multipart is enabled.
	 * @return the result
	 */
	public boolean isLogMultipart() {
		return logMultipart;
	}

	/** Sets the log multipart.
	 * @param logMultipart the logMultipart
	 */
	public void setLogMultipart(boolean logMultipart) {
		this.logMultipart = logMultipart;
	}

	/** Returns whether the pretty logging is enabled.
	 * @return the result
	 */
	public boolean isPrettyLogging() {
		return prettyLogging;
	}

	/** Sets the pretty logging.
	 * @param prettyLogging the prettyLogging
	 */
	public void setPrettyLogging(boolean prettyLogging) {
		this.prettyLogging = prettyLogging;
	}

	/** Returns whether the verbose is enabled.
	 * @return the result
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/** Sets the verbose.
	 * @param verbose the verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
}
