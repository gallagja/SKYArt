#version 120

precision mediump float;

uniform vec3 vColor;

varying vec3 position;


void main() {
    gl_FragColor = vec4(vColor, 1.0);
}