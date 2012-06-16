package org.lazan.t5.cometd.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class FakeHttpServletResponse implements HttpServletResponse {
	private final OutputStream outputStream;
	private final String charsetName;
	
	public FakeHttpServletResponse(OutputStream outputStream, String charsetName) {
		super();
		this.outputStream = outputStream;
		this.charsetName = charsetName;
	}

	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException("flushBuffer");
	}

	public int getBufferSize() {
		throw new UnsupportedOperationException("getBufferSize");
	}

	public String getCharacterEncoding() {
		throw new UnsupportedOperationException("getCharacterEncoding");
	}

	public String getContentType() {
		throw new UnsupportedOperationException("getContentType");
	}

	public Locale getLocale() {
		throw new UnsupportedOperationException("getLocale");
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStream() {
			public void write(int b) throws IOException {
				outputStream.write(b);
			}
		};
	}

	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(new OutputStreamWriter(outputStream, charsetName));
	}

	public boolean isCommitted() {
		throw new UnsupportedOperationException("isCommitted");
	}

	public void reset() {
		throw new UnsupportedOperationException("reset");
	}

	public void resetBuffer() {
		throw new UnsupportedOperationException("resetBuffer");
	}

	public void setBufferSize(int arg0) {
		throw new UnsupportedOperationException("setBufferSize");
	}

	public void setCharacterEncoding(String arg0) {
		throw new UnsupportedOperationException("setCharacterEncoding");
	}

	public void setContentLength(int arg0) {
		throw new UnsupportedOperationException("setContentLength");
	}

	public void setContentType(String arg0) {
		throw new UnsupportedOperationException("setContentType");
	}

	public void setLocale(Locale arg0) {
		throw new UnsupportedOperationException("setLocale");
	}

	public void addCookie(Cookie arg0) {
		throw new UnsupportedOperationException("addCookie");
	}

	public void addDateHeader(String arg0, long arg1) {
		throw new UnsupportedOperationException("addDateHeader");
	}

	public void addHeader(String arg0, String arg1) {
		throw new UnsupportedOperationException("addHeader");
	}

	public void addIntHeader(String arg0, int arg1) {
		throw new UnsupportedOperationException("addIntHeader");
	}

	public boolean containsHeader(String arg0) {
		throw new UnsupportedOperationException("containsHeader");
	}

	public String encodeRedirectURL(String arg0) {
		throw new UnsupportedOperationException("encodeRedirectURL");
	}

	public String encodeRedirectUrl(String arg0) {
		throw new UnsupportedOperationException("encodeRedirectUrl");
	}

	public String encodeURL(String arg0) {
		throw new UnsupportedOperationException("encodeURL");
	}

	public String encodeUrl(String arg0) {
		throw new UnsupportedOperationException("encodeUrl");
	}

	public void sendError(int arg0, String arg1) throws IOException {
		throw new UnsupportedOperationException("sendError");
	}

	public void sendError(int arg0) throws IOException {
		throw new UnsupportedOperationException("sendError");
	}

	public void sendRedirect(String arg0) throws IOException {
		throw new UnsupportedOperationException("sendRedirect");
	}

	public void setDateHeader(String arg0, long arg1) {
		throw new UnsupportedOperationException("setDateHeader");
	}

	public void setHeader(String arg0, String arg1) {
		throw new UnsupportedOperationException("setHeader");
	}

	public void setIntHeader(String arg0, int arg1) {
		throw new UnsupportedOperationException("setIntHeader");
	}

	public void setStatus(int arg0, String arg1) {
		throw new UnsupportedOperationException("setStatus");
	}

	public void setStatus(int arg0) {
		throw new UnsupportedOperationException("setStatus");
	}
}
