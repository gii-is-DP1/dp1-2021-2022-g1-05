package org.springframework.samples.parchisYOca.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
public class SessionController {

    public static Integer NUM_DICES=2;
    public static Integer NUM_DICES_SIDES=6;


    //Array where the first NUM_DICES indexes are the dices and the NUM_DICES+1 the sum of both
    @GetMapping("session/rolldices")
    public String rollDices(HttpSession session, HttpServletRequest request){
        int[] dices = new int[NUM_DICES+1];
        for (Integer i = 0; i<NUM_DICES; i++){
            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
        }
        dices[NUM_DICES] = Arrays.stream(dices).sum();
        session.setAttribute("dices", dices);

        //Redirects the user to the goose controller
        Object refererGoose = request.getSession().getAttribute("fromGoose");
        if(refererGoose != null){
            return "redirect:/gooseInGame/dicesRolled";
        }

        return "redirect:/";

    }


}
