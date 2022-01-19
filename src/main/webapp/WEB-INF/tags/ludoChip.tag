<%@ attribute name="size" required="true" rtexprvalue="true" 
 description="Size of the to show" %>
 <%@ attribute name="chip" required="true" rtexprvalue="true" type="org.springframework.samples.parchisYOca.ludoChip.LudoChip"
 description="Piece to be rendered" %>
 <%@ attribute name="position" required="true" rtexprvalue="true" 
 description="Position of the chip to show" %>
 <%@ attribute name="chipsToBeDisplaced" required="true" rtexprvalue="true" type="java.util.List"
 description="Position of the chip to show" %>
 <script>
 var canvas = document.getElementById("canvas");
 var ctx = canvas.getContext("2d");
 var image = document.getElementById('${chip.getColor()}');

 ctx.drawImage(image,${chip.getX(chip.color,chip.inGameChipId, chip.gameState, chipsToBeDisplaced.contains(chip), position)},${chip.getY(chip.color,chip.inGameChipId, chip.gameState, chipsToBeDisplaced.contains(chip), position)},${size},${size});
 </script>
