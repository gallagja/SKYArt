//precision mediump float;
precision highp float;

varying vec3 vertPosition;

void main() {
    gl_FragColor = vec4(vertPosition, 1.0);
}
