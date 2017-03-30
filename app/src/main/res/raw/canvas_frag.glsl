precision mediump float;

uniform mat4 vModelView;
uniform vec4 vColor;

varying vec3 position;
varying vec3 normal;

uniform sampler2D u_Texture;
varying vec2 v_TexCoordinate;
void main() {
//    gl_FragColor.rgb = vColor.rgb;
    gl_FragColor = vColor;
//    gl_FragColor.a = .01;
//gl_FragColor = ( texture2D(u_Texture, v_TexCoordinate).rgba);
}