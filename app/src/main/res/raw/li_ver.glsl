

uniform vec4  lightPositionOC;   // in object coordinates
uniform vec3  spotDirectionOC;   // in object coordinates
uniform float spotCutoff;        // in degrees

 attribute vec4 a_Position; // Per-vertex position information we will pass in.
 attribute vec4 a_Color;    // Per-vertex color information we will pass in.
 attribute vec3 a_Normal;   // Per-vertex normal information we will pass in.
 attribute vec3 vertexPosition; // comes from VertexBuffer
 varying vec4 v_Color;      // This will be passed into the fragment shader.
void main(void)
{
   vec3 lightPosition;
   vec3 spotDirection;
   vec3 lightDirection;
   float angle;

    gl_Position = a_Position;

    // Transforms light position and direction into eye coordinates
    lightPosition  = vertexPosition;
    spotDirection  = normalize(spotDirectionOC * a_Normal);

    // Calculates the light vector (vector from light position to vertex)
    vec4 vertex =  a_Position;
    lightDirection = normalize(vertex.xyz - lightPosition.xyz);

    // Calculates the angle between the spot light direction vector and the light vector
    angle = dot( normalize(spotDirection), -normalize(lightDirection));
    //angle = max(angle,0);

   // Test whether vertex is located in the cone
   if(angle > radians(spotCutoff))
      v_Color = vec4(1,1,0,1); // lit (yellow)

}