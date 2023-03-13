package com.rosevii.filter;

import com.alibaba.fastjson.JSON;
import com.rosevii.common.BaseContext;
import com.rosevii.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: rosevvi
 * @date: 2023/3/6 15:28
 * @version: 1.0
 * @description:
 * 检查用户是否完成登录
 */

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher ANT_PATH_MATCHER= new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        //1、获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求"+requestURI);

        //定义不需要处理的路径 /backend/index.html 和定义的不匹配所以用 AntPathMatcher
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if (check){
            log.info("本次请求不需要处理{}",requestURI);
            chain.doFilter(request,response);
            return;
        }
        //4、判断登录状态，如果已登录则直接放行
        if (request.getSession().getAttribute("employee") != null ){
            log.info("用户已登陆，用户id为：{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrent(empId);
            chain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配 检车本次请求是否需要放行
     * @param urls
     * @param requestUri
     * @return
     */
    public boolean check(String[] urls,String requestUri){
        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, requestUri);
            if (match){
                //匹配上了
                return true;
            }
        }
        return false;
    }
}
