

 uniform mat4 u_MVPMatrix;  // A constant representing the combined model/view/projection matrix.
 uniform mat4 u_MVMatrix;   // A constant representing the combined model/view matrix.
 uniform vec3 u_LightPos;   // The position of the light in eye space.

 attribute vec4 a_Position; // Per-vertex position information we will pass in.
 attribute vec4 a_Color;    // Per-vertex color information we will pass in.
 attribute vec3 a_Normal;   // Per-vertex normal information we will pass in.

 varying vec4 v_Color;      // This will be passed into the fragment shader.

 void main()                // The entry point for our vertex shader.
 {                             //ransform the vertex into eye space.
      vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
//Transform the normal's orientation into eye space.
      vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
//Will be used for attenuation.
      float distance = length(u_LightPos - modelViewVertex);
//Get a lighting direction vector from the light to the vertex.
      vec3 lightVector = normalize(u_LightPos - modelViewVertex);
//Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
//pointing in the same direction then it will get max illumination.
      float diffuse = max(dot(modelViewNormal, lightVector), 0.1);
//Attenuate the light based on distance.
      diffuse = diffuse * (1.0);
//Multiply the color by the illumination level. It will be interpolated across the triangle.
      v_Color = a_Color * diffuse;
//gl_Position is a special variable used to store the final position.
//Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
     gl_Position = u_MVPMatrix * a_Position;
}
//   //calculate normal in world coordinates
//   mat3 normalMatrix = transpose(inverse(mat3(model)));
//   vec3 normal = normalize(normalMatrix * fragNormal);
//
//   //calculate the location of this fragment (pixel) in world coordinates
//   vec3 fragPosition = vec3(model * vec4(fragVert, 1));
//
//   //calculate the vector from this pixels surface to the light source
//   vec3 surfaceToLight = light.position - fragPosition;

//   //calculate the cosine of the angle of incidence
//   float brightness = dot(normal, surfaceToLight) / (length(surfaceToLight) * length(normal));
//   brightness = clamp(brightness, 0, 1);

//   //calculate final color of the pixel, based on:
//   // 1. The angle of incidence: brightness
//   // 2. The color/intensities of the light: light.intensities
//   // 3. The texture and texture coord: texture(tex, fragTexCoord)
//   vec4 surfaceColor = texture(tex, fragTexCoord);
//   finalColor = vec4(brightness * light.intensities * surfaceColor.rgb, surfaceColor.a);