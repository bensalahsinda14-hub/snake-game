package com.devops.game;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    
    private GameEngine gameEngine;

    @Override
    public void init() throws ServletException {
        super.init();
        gameEngine = new GameEngine();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Snake Game</title>");
        out.println("<style>body{font-family:Arial;text-align:center;background:#667eea;padding:50px;}");
        out.println("canvas{border:3px solid #333;background:#f0f0f0;}</style></head>");
        out.println("<body><h1>🐍 Snake Game - DevOps Project</h1>");
        out.println("<p>Score: <span id='score'>0</span></p>");
        out.println("<canvas id='c' width='400' height='400'></canvas>");
        out.println("<p>Version: " + gameEngine.getVersion() + "</p>");
        out.println("<script>");
        out.println("var c=document.getElementById('c');var ctx=c.getContext('2d');");
        out.println("var s=[{x:200,y:200}];var d='R';var f={x:180,y:180};var sc=0;");
        out.println("document.onkeydown=function(e){");
        out.println("if(e.keyCode==37&&d!='R')d='L';");
        out.println("if(e.keyCode==38&&d!='D')d='U';");
        out.println("if(e.keyCode==39&&d!='L')d='R';");
        out.println("if(e.keyCode==40&&d!='U')d='D';};");
        out.println("function g(){ctx.fillStyle='#f0f0f0';ctx.fillRect(0,0,400,400);");
        out.println("for(var i=0;i<s.length;i++){ctx.fillStyle=i==0?'#667eea':'#764ba2';");
        out.println("ctx.fillRect(s[i].x,s[i].y,20,20);}");
        out.println("ctx.fillStyle='red';ctx.fillRect(f.x,f.y,20,20);");
        out.println("var x=s[0].x,y=s[0].y;");
        out.println("if(d=='L')x-=20;if(d=='U')y-=20;if(d=='R')x+=20;if(d=='D')y+=20;");
        out.println("if(x==f.x&&y==f.y){sc++;document.getElementById('score').textContent=sc;");
        out.println("f={x:Math.floor(Math.random()*20)*20,y:Math.floor(Math.random()*20)*20};}else s.pop();");
        out.println("if(x<0||y<0||x>=400||y>=400){alert('Game Over! Score:'+sc);location.reload();}");
        out.println("s.unshift({x:x,y:y});}setInterval(g,100);");
        out.println("</script></body></html>");
    }
}
