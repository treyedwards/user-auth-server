package com.tec.server.authserver.filter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException, java.io.IOException {
      HttpServletResponse response = (HttpServletResponse) res;
      response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
      chain.doFilter(req, res);
  }
}