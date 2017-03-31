attribute vec3 vertexPosition; // comes from VertexBuffer

uniform mat4 vModelView;
uniform mat4 vProjection;


// Depending on if we stay with this version of GLSL, we
//  may need to use out and in. ("out" in the vertex shader
//  and "in" in the frag)
varying vec3 position;
varying vec3 normal;

varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in.


void main()
{
    // Once the vertex is multiplied by the Model View, the vertex
    //  is brought into our camera space and the rotatation/translation
    //  is applied.
    gl_Position = vec4(vertexPosition.xyz, 1.0) * vModelView;

    // Give the fragment shader the unprojected vertex position,
    //  just incase it wants to play with it.
    position = gl_Position.xyz;

    // Since the Cylinder is always guaranteed to face inward, the normal is simpley
    //  the negated vertexPosition minus the z component.
    normal = normalize( (-vec4(vertexPosition.xy, 0.0, 0.0)) * vModelView ).xyz;

    // Once it is projected the vertex will have perspective,
    //  you know that thing that makes objects look bigger when
    //  it's closer and smaller when farther out.
    gl_Position = gl_Position * vProjection;

    v_TexCoordinate = a_TexCoordinate;
}