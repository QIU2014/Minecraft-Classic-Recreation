#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec2 v_texCoord;
varying vec4 v_color;

void main() {
    vec4 tex = texture2D(u_texture, v_texCoord);
    gl_FragColor = tex * v_color;
}
