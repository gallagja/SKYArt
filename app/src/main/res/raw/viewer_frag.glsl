//precision mediump float;
precision highp float;

uniform mat4 vModelView;
uniform vec4 vColor;

uniform vec4 vSprayerColor;
uniform float vSprayerHalfAngle;

varying vec3 position;
varying vec3 normal;

varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.
uniform sampler2D u_Texture;

void main() {
    float intens = dot(normalize(position), vec3(0.0, 0.0, -1.0));
    float angle = degrees(acos(intens));


    if (angle < vSprayerHalfAngle) {
        intens *= 1.0 - (angle/vSprayerHalfAngle);

        float sprayerRadius = 1.0;
        vec3 sprayToSurface = - position;
        float distance = length(sprayToSurface);
        float d = max(distance - sprayerRadius, 0.0);
        sprayToSurface /= distance;

        // calculate basic attenuation
        float denom = (d/sprayerRadius) + 1.0;
        float attenuation = 1.0 / (denom*denom);
        float cutoff = 2.0;
        attenuation = (attenuation - cutoff) / (1.0 - cutoff);
        attenuation = max(attenuation, 0.0);
        intens *= attenuation;

        intens = max(0.0, min(intens, 1.0));
       // gl_FragColor.rgb = (vec3(intens) * vSprayerColor.rgb) + ((vec3(1.0-intens)*vColor.rgb) * texture2D(u_Texture, v_TexCoordinate).rgb);

        //gl_FragColor.a = ((intens) * vSprayerColor.a) + ((1.0-intens)*vColor.a);
    //    gl_FragColor.a = intens;


    }
    else {
        gl_FragColor = vColor;
    }
       gl_FragColor.rgb = vec3(v_TexCoordinate, position.x);
}