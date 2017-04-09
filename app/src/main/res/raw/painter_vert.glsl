attribute vec3 painterPosition; // comes from VertexBuffer

uniform sampler2D texture;

varying vec3 vertPosition;

void main()
{
    vertPosition = painterPosition;

}