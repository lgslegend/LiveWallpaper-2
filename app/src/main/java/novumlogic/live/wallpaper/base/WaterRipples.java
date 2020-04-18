package novumlogic.live.wallpaper.base;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import novumlogic.live.wallpaper.gdx.Gdx;
import novumlogic.live.wallpaper.gdx.graphics.Camera;
import novumlogic.live.wallpaper.gdx.graphics.Mesh;
import novumlogic.live.wallpaper.gdx.graphics.Texture;
import novumlogic.live.wallpaper.gdx.graphics.VertexAttribute;
import novumlogic.live.wallpaper.gdx.graphics.VertexAttributes;
import novumlogic.live.wallpaper.gdx.graphics.glutils.ShaderProgram;
import novumlogic.live.wallpaper.gdx.math.Intersector;
import novumlogic.live.wallpaper.gdx.math.Plane;
import novumlogic.live.wallpaper.gdx.math.Vector2;
import novumlogic.live.wallpaper.gdx.math.Vector3;
import novumlogic.live.wallpaper.gdx.math.collision.Ray;

class WaterRipples {
    private final float DAMPING = 0.9f;
    private final float DISPLACEMENT = -10;
    private final float TICK = 0.028f;
    private final int RADIUS = 3;
    final static short CellSuggestedDimension = 26;
    private float accum;
    private Mesh mesh;
    private Plane plane;
    private Vector3 point3 = new Vector3();
    private Vector2 point2 = new Vector2();
    private float[][] last;
    private float[][] curr;
    private float zDepthCoord = 0;
    private float posx;
    private float posy;
    private int width;
    private int height;
    private ShaderProgram shader;
    private Texture mTexture;
    private float[] vertices;
    private boolean updateDirectBufferAccess = false;
    private boolean noripple = false;
    private final float NO_RIPPLE_TOLERANCE = 0.05f;
    private boolean computingTouchArray = false;

    private static final String ShaderProgram_PROJECTION_VIEW_MATRIX_UNIFORM = "u_projectionViewMatrix";
    private static final String ShaderProgram_TEXTURE_SAMPLER_UNIFORM = "u_texture";

    private static final String ShaderProgram_VERTEX_COLOR_UNIFORM = "u_color";
    private static final String ShaderProgram_FLIP_TEXTURE_X_UNIFORM = "u_flipTexU";
    private static final String ShaderProgram_FLIP_TEXTURE_Y_UNIFORM = "u_flipTexV";

    WaterRipples(float z, float xpos, float ypos, int w, int h, Texture tex) {
        createShaders();
        updateZ(z);
        mTexture = tex;
        posx = xpos;
        posy = ypos;
        width = w;
        height = h;

        last = new float[width + 1][height + 1];
        curr = new float[width + 1][height + 1];
        int nIndices = width * height * 6;
        int nVertices = (width + 1) * (height + 1);
        mesh = new Mesh(false, nVertices, nIndices, new VertexAttribute(
                VertexAttributes.Usage.Position, 3,
                ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(
                VertexAttributes.Usage.TextureCoordinates, 2,
                ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        // init indices
        short[] indices = new short[nIndices];
        int idx = 0;
        short vidx = 0;
        for (int y = 0; y < height; y++) {
            vidx = (short) (y * (width + 1));

            for (int x = 0; x < width; x++) {
                indices[idx++] = vidx;
                indices[idx++] = (short) (vidx + 1);
                indices[idx++] = (short) (vidx + width + 1);

                indices[idx++] = (short) (vidx + 1);
                indices[idx++] = (short) (vidx + width + 2);
                indices[idx++] = (short) (vidx + width + 1);

                vidx++;
            }
        }
        mesh.setIndices(indices);

        vertices = new float[nVertices * 5];
        idx = 0;
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {

                vertices[idx++] = 0;
                vertices[idx++] = 0;
                vertices[idx++] = 0;
                vertices[idx++] = 0;
                vertices[idx++] = 0;
            }
        }
        mesh.setVertices(vertices);
        updateVertices(0);

        noripple = true;
    }

    void updateZ(float z) {
        zDepthCoord = z;
        plane = new Plane(new Vector3(0, 0, zDepthCoord), new Vector3(1, 0,
                zDepthCoord), new Vector3(0, 1, zDepthCoord));
    }

    private float interpolate(float alpha, int x, int y) {
        return alpha * last[x][y] + (1 - alpha) * curr[x][y];
    }

    private void updateVertices(float alpha) {

        FloatBuffer buffer = null;
        float floatsPerVertex = mesh.getVertexSize() / 4f;

        if (updateDirectBufferAccess) {
            int numVertices = mesh.getNumVertices();
            float floatBufferSize = numVertices * floatsPerVertex;

            buffer = mesh.getVerticesBuffer();
            assert (floatBufferSize == buffer.limit());
        }

        noripple = true;

        int idx = 0;
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                float xOffset = 0;
                float yOffset = 0;

                if (x > 0 && x < width && y > 0 && y < height) {

                    float c1 = interpolate(alpha, x - 1, y);
                    float c2 = interpolate(alpha, x + 1, y);
                    float c3 = interpolate(alpha, x, y - 1);
                    float c4 = interpolate(alpha, x, y + 1);

                    xOffset = (c1 - c2);
                    yOffset = (c3 - c4);

                    noripple = noripple && xOffset >= -NO_RIPPLE_TOLERANCE
                            && xOffset <= NO_RIPPLE_TOLERANCE
                            && yOffset >= -NO_RIPPLE_TOLERANCE
                            && yOffset <= NO_RIPPLE_TOLERANCE;
                }

                if (updateDirectBufferAccess) {
                    buffer.put(idx + 0, x + posx); // x
                    buffer.put(idx + 1, y + posy); // y
                    buffer.put(idx + 2, zDepthCoord); // z
                } else {
                    vertices[idx++] = x + posx; // x
                    vertices[idx++] = y + posy; // y
                    vertices[idx++] = zDepthCoord; // z
                }

                float u = (x + xOffset) / (float) width;
                float v = 1 - ((y + yOffset) / (float) height); // (FLIPPED)

                float wRatio = 0;
                float hRatio = 0;
                float _width = mTexture.getWidth();
                float _height = mTexture.getHeight();

                wRatio = _width / (float) width;
                hRatio = _height / (float) height;

                u = ((x + xOffset) * wRatio + 0) / _width;
                v = 1 - (((y + yOffset) * hRatio + 0) / _height);

                if (updateDirectBufferAccess) {
                    buffer.put(idx + 3, u); // u
                    buffer.put(idx + 4, v); // v
                    idx += floatsPerVertex;
                } else {
                    vertices[idx++] = u; // u
                    vertices[idx++] = v; // v
                }
            }
        }

        if (!updateDirectBufferAccess) {
            mesh.setVertices(vertices);
        }
    }

    private void touchWater(Vector2 point) {
        computingTouchArray = true;
        float px = point.x - posx;
        float py = point.y - posy;

        for (int y = Math.max(0, (int) py - RADIUS); y < Math.min(height,
                (int) py + RADIUS); y++) {
            for (int x = Math.max(0, (int) px - RADIUS); x < Math.min(width,
                    (int) px + RADIUS); x++) {

//                 point.dst2(x, y, zDepthCoord)
                float a = x - px;
                float b = y - py;
                a *= a;
                b *= b;
                float dst2 = a + b;

                float val = curr[x][y]
                        + DISPLACEMENT
                        * Math.max(
                        0,
                        (float) Math.cos(Math.PI / 2 * Math.sqrt(dst2)
                                / (float) RADIUS));
                if (val < DISPLACEMENT)
                    val = DISPLACEMENT;
                else if (val > -DISPLACEMENT)
                    val = -DISPLACEMENT;
                curr[x][y] = val;
                curr[x][y] = val;
            }
        }
        computingTouchArray = false;
        noripple = false;
    }

    void touchScreen(Camera camera, int x, int y) {
        Ray ray = camera.getPickRay(x, y);
        Intersector.intersectRayPlane(ray, plane, point3);
        touchWater(point2.set(point3.x, point3.y));
    }

    void render(Camera camera, boolean directBufferAccess) {
        updateDirectBufferAccess = directBufferAccess;

        if (noripple) {
            accum = TICK;
            Gdx.gl20.glActiveTexture(GL10.GL_TEXTURE);
            Gdx.gl20.glEnable(GL10.GL_TEXTURE_2D);

            shader.begin();
            shader.setUniformMatrix(ShaderProgram_PROJECTION_VIEW_MATRIX_UNIFORM, camera.combined);
            shader.setUniformf(ShaderProgram_VERTEX_COLOR_UNIFORM, 1, 1, 1, 1);
            shader.setUniformi(ShaderProgram_TEXTURE_SAMPLER_UNIFORM, 1);
            shader.setUniformi(ShaderProgram_TEXTURE_SAMPLER_UNIFORM + 0, 0);
            mesh.render(shader, GL10.GL_TRIANGLES);
            shader.end();
            return;
        }

        accum += Gdx.graphics.getDeltaTime();

        while (accum > TICK) {
            while (computingTouchArray) {
                updateDirectBufferAccess = directBufferAccess;
            }

            // ripple update
            for (int y = 0; y <= height; y++) {
                for (int x = 0; x <= width; x++) {
                    if (x > 0 && x < width && y > 0 && y < height) {
                        curr[x][y] = (last[x - 1][y] + last[x + 1][y]
                                + last[x][y + 1] + last[x][y - 1])
                                / 2 - curr[x][y];
                    }
                    curr[x][y] *= DAMPING;
                }
            }

            float[][] tmp = curr;
            curr = last;
            last = tmp;
            accum -= TICK;
        }

        float alpha = accum / TICK;

        updateVertices(alpha);

        Gdx.gl20.glActiveTexture(GL10.GL_TEXTURE);
        Gdx.gl20.glEnable(GL10.GL_TEXTURE_2D);

        shader.begin();
        shader.setUniformMatrix(ShaderProgram_PROJECTION_VIEW_MATRIX_UNIFORM, camera.combined);
        shader.setUniformf(ShaderProgram_VERTEX_COLOR_UNIFORM, 1, 1, 1, 1);
        shader.setUniformi(ShaderProgram_TEXTURE_SAMPLER_UNIFORM, 1);
        shader.setUniformi(ShaderProgram_TEXTURE_SAMPLER_UNIFORM + 0, 0);
        mesh.render(shader, GL10.GL_TRIANGLES);
        shader.end();
    }

    private void createShaders() {
        String vertexShader = createVertexShader(false, false, 1);
        String fragmentShader = createFragmentShader(false, false, 1);
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled())
            throw new IllegalArgumentException("Couldn't compile shader: " + shader.getLog());
    }

    private String createVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords) {

        String shader = "#ifdef GL_ES\n" + "#define LOWP lowp\n"
                + "precision mediump float;\n" + "#else\n" + "#define LOWP \n"
                // + "precision highp float;\n"
                + "#endif\n";

        shader += "attribute vec4 "
                + ShaderProgram.POSITION_ATTRIBUTE
                + ";\n"
                + (hasNormals ? "attribute vec3 "
                + ShaderProgram.NORMAL_ATTRIBUTE + ";\n" : "")
                + (hasColors ? "attribute vec4 "
                + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");

        for (int i = 0; i < numTexCoords; i++) {
            shader += "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + i
                    + ";\n";
        }

        shader += "uniform mat4 "
                + ShaderProgram_PROJECTION_VIEW_MATRIX_UNIFORM + ";\n";

        shader += (hasColors ? "varying LOWP vec4 v_col;\n" : "");

        for (int i = 0; i < numTexCoords; i++) {
            shader += "varying vec2 v_tex" + i + ";\n";
        }

        shader += "void main() {\n"
                + "   gl_Position = "
                + ShaderProgram_PROJECTION_VIEW_MATRIX_UNIFORM
                + " * "
                + ShaderProgram.POSITION_ATTRIBUTE
                + ";\n"
                + (hasColors ? "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE
                + ";\n" : "");

        for (int i = 0; i < numTexCoords; i++) {
            shader += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE
                    + i + ";\n";
        }

        shader += "}\n";

        return shader;
    }

    private String createFragmentShader(boolean hasNormals, boolean hasColors,
                                        int numTexCoords) {

        String shader = "#ifdef GL_ES\n" + "#define LOWP lowp\n"
                + "precision mediump float;\n" + "#else\n" + "#define LOWP \n"
                // + "precision highp float;\n"
                + "#endif\n";

        if (hasColors)
            shader += "varying LOWP vec4 v_col;\n";

        for (int i = 0; i < numTexCoords; i++) {
            shader += "varying vec2 v_tex" + i + ";\n";
            shader += "uniform sampler2D "
                    + ShaderProgram_TEXTURE_SAMPLER_UNIFORM + i + ";\n";
        }

        if (numTexCoords == 2) {
            shader += "uniform int " + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 0
                    + ";\n";
            shader += "uniform int " + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 0
                    + ";\n";
            shader += "uniform int " + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 1
                    + ";\n";
            shader += "uniform int " + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 1
                    + ";\n";
        }

        shader += "uniform vec4 " + ShaderProgram_VERTEX_COLOR_UNIFORM + ";\n";

        shader += "uniform int " + ShaderProgram_TEXTURE_SAMPLER_UNIFORM
                + ";\n";

        shader += "void main() {\n";

        if (numTexCoords == 2) {
            shader += "vec2 texCoord" + 0 + " = v_tex" + 0 + ";" + "\n if ("
                    + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 0 + " == 1 && "
                    + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 0
                    + " == 0) { texCoord" + 0 + " = vec2(1.0 - v_tex" + 0
                    + ".s, v_tex" + 0 + ".t);}"

                    + "\n if (" + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 0
                    + " == 1 && " + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 0
                    + " == 1) { texCoord" + 0 + " = vec2(1.0 - v_tex" + 0
                    + ".s, 1.0 - v_tex" + 0 + ".t);}"

                    + "\n if (" + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 0
                    + " == 0 && " + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 0
                    + " == 1) { texCoord" + 0 + " = vec2(v_tex" + 0
                    + ".s, 1.0 - v_tex" + 0 + ".t);}"

                    + "\n vec4 texture" + 0 + " = ("
                    + ShaderProgram_TEXTURE_SAMPLER_UNIFORM
                    + " == 1 ? texture2D("
                    + ShaderProgram_TEXTURE_SAMPLER_UNIFORM + 0 + ",  texCoord"
                    + 0 + ") : vec4(1,1,1,1));";

            shader += "vec2 texCoord" + 1 + " = v_tex" + 1 + ";" + "\n if ("
                    + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 1 + " == 1 && "
                    + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 1
                    + " == 0) { texCoord" + 1 + " = vec2(1.0 - v_tex" + 1
                    + ".s, v_tex" + 1 + ".t);}"

                    + "\n if (" + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 1
                    + " == 1 && " + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 1
                    + " == 1) { texCoord" + 1 + " = vec2(1.0 - v_tex" + 1
                    + ".s, 1.0 - v_tex" + 1 + ".t);}"

                    + "\n if (" + ShaderProgram_FLIP_TEXTURE_X_UNIFORM + 1
                    + " == 0 && " + ShaderProgram_FLIP_TEXTURE_Y_UNIFORM + 1
                    + " == 1) { texCoord" + 1 + " = vec2(v_tex" + 1
                    + ".s, 1.0 - v_tex" + 1 + ".t);}"

                    + "\n vec4 texture" + 1 + " = ("
                    + ShaderProgram_TEXTURE_SAMPLER_UNIFORM
                    + " == 1 ? texture2D("
                    + ShaderProgram_TEXTURE_SAMPLER_UNIFORM + 1 + ",  texCoord"
                    + 1 + ") : vec4(1,1,1,1));";

            shader += " if (" + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".r == 0.0 && " + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".g == 0.0 && " + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".b == 0.0 && " + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".a == 0.0 ) { \n gl_FragColor = "
                    + (hasColors ? "v_col" : "vec4(1, 0, 0, 0.5)")
                    + ";\n} else { \n gl_FragColor = "
                    + ShaderProgram_VERTEX_COLOR_UNIFORM + "; \n}";

            shader += "\n vec3 col = mix(texture0.rgb, texture1.rgb, texture1.a);";
            shader += "\n gl_FragColor = gl_FragColor * vec4(col, texture0.a);";

        } else {
            shader += " if (" + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".r == 0.0 && " + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".g == 0.0 && " + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".b == 0.0 && " + ShaderProgram_VERTEX_COLOR_UNIFORM
                    + ".a == 0.0 ) { \n gl_FragColor = "
                    + (hasColors ? "v_col" : "vec4(1, 0, 0, 0.5)")
                    + ";\n} else { \n gl_FragColor = "
                    + ShaderProgram_VERTEX_COLOR_UNIFORM + "; \n}";

            if (numTexCoords > 0)
                shader += " gl_FragColor = gl_FragColor * ";

            for (int i = 0; i < numTexCoords; i++) {

                shader += " (" + ShaderProgram_TEXTURE_SAMPLER_UNIFORM
                        + " == 1 ? texture2D("
                        + ShaderProgram_TEXTURE_SAMPLER_UNIFORM + i
                        + ",  v_tex" + i + ") : vec4(1,1,1,1))";

                if (i != numTexCoords - 1) {
                    shader += "*";
                }
            }
        }

        shader += ";\n}";

        return shader;
    }

    public Mesh createFullScreenQuad() {

        float[] verts = new float[20];
        int i = 0;

        verts[i++] = -1; // x1
        verts[i++] = -1; // y1
        verts[i++] = 0;
        verts[i++] = 0f; // u1
        verts[i++] = 0f; // v1

        verts[i++] = 1f; // x2
        verts[i++] = -1; // y2
        verts[i++] = 0;
        verts[i++] = 1f; // u2
        verts[i++] = 0f; // v2

        verts[i++] = 1f; // x3
        verts[i++] = 1f; // y2
        verts[i++] = 0;
        verts[i++] = 1f; // u3
        verts[i++] = 1f; // v3

        verts[i++] = -1; // x4
        verts[i++] = 1f; // y4
        verts[i++] = 0;
        verts[i++] = 0f; // u4
        verts[i++] = 1f; // v4

        Mesh mesh = new Mesh( true, 4, 0,  // static mesh with 4 vertices and no indices
                new VertexAttribute( VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE ),
                new VertexAttribute( VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" ) );

        mesh.setVertices( verts );
        return mesh;
    }
}
