precision mediump float;

uniform mat4 vModelView;
uniform vec4 vColor;

varying vec3 position;
varying vec3 normal;

void main() {
//    gl_FragColor.rgb = vColor.rgb;
    gl_FragColor = vColor;
//    gl_FragColor.a = .01f;
}