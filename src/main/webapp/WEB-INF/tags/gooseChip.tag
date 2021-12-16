<%@ attribute name="size" required="true" rtexprvalue="true" 
 description="Size of the to show" %>
 <%@ attribute name="chip" required="true" rtexprvalue="true" type="org.springframework.samples.parchisYOca.gooseChip.GooseChip"
 description="Piece to be rendered" %>
 <%@ attribute name="position" required="true" rtexprvalue="true" 
 description="Position of the chip to show" %>
 <script>
 var canvas = document.getElementById("canvas");
 var ctx = canvas.getContext("2d");
 var image = document.getElementById('${chip.inGameId}');
 ctx.drawImage(image,${chip.getPositionXInPixels(position)},${chip.getPositionYInPixels(position)},${size},${size});
 </script>