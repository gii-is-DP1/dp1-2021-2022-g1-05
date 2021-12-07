package org.springframework.samples.parchisYOca.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
public class SessionController {

    public static Integer NUM_DICES=2;
    public static Integer NUM_DICES_SIDES=6;

    @GetMapping("session/rolldices")
    public @ResponseBody int[] rollDices(HttpSession session){
        int[] dices = new int[NUM_DICES];
        for (Integer i = 0; i<NUM_DICES; i++){
            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
        }
        session.setAttribute("dices", dices);
        return dices;
    }

    @GetMapping("session/sumdices")
    public @ResponseBody Integer sumDices(HttpSession session){
        int[] dices = (int[])session.getAttribute("dices");
        return Arrays.stream(dices).sum();
    }

}
