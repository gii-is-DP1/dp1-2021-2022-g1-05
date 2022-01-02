<%@ attribute name="ludoBoard" required="false" rtexprvalue="true" type="org.springframework.samples.parchisYOca.ludoBoard.LudoBoard"
 description="ludoBoard to be rendered" %>
<canvas id="canvas" width="${ludoBoard.width}" height="${ludoBoard.height}"></canvas>
<img id="source" src="${ludoBoard.background}" style="display:none">

<script>
var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");
var image = document.getElementById('source');

ctx.drawImage(image, 0, 0, ${ludoBoard.width}, ${ludoBoard.height});
</script>
