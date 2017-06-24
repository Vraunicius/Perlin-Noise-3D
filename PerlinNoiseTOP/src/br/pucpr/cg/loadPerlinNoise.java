package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Mesh;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Shader;
import br.pucpr.mage.Window;
import br.pucpr.mage.phong.DirectionalLight;

import br.pucpr.mage.phong.Material;

import br.pucpr.mage.Texture;


public class loadPerlinNoise implements Scene {

    private Keyboard keys = Keyboard.getInstance();
    private boolean normals = false;
    private createPerlinNoise perlin = new createPerlinNoise();
    private float angleX = 0.0f;
    private float angleY = 0.5f;
    private Camera camera = new Camera();
    private static final String PATH = "img/opengl/textures/brickwall_t.jpg";
    private DirectionalLight light = new DirectionalLight(
            //direction
            new Vector3f( 1.0f, -1.0f, -1.0f),
            //ambient
            new Vector3f( 0.5f,  0.5f,  0.5f),
            //diffuse
            new Vector3f( 1.0f,  1.0f,  0.8f),
            //specular
            new Vector3f( 1.0f,  1.0f,  1.0f));
    private Mesh mesh;
    Material material = new Material(
            //ambient
            new Vector3f(0.5f, 0.5f, 0.5f),
            //diffuse
            new Vector3f(0.5f, 0.5f, 0.5f),
            //specular
            new Vector3f(0.5f, 0.5f, 0.5f),
            100.0f);

    float scaleMoutain=1;

    @Override
    public void init() {
        perlin.run();
        material.setTexture("uTexture", new Texture(PATH));

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(255.0f, 0.0f, 255.0f, 1.0f);

        try {
            mesh = MeshFactory.loadTerrain(new File("img/opengl/heights/volcano.png"), 0.5f,3);
            System.out.println("AMEM");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        camera.getPosition().y = 100.0f;
        camera.getPosition().z = 300.0f;
    }

    @Override
    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }

        if (keys.isDown(GLFW_KEY_A)) {
            angleY += secs;
        }

        if (keys.isDown(GLFW_KEY_D)) {
            angleY -= secs;
        }

        if (keys.isDown(GLFW_KEY_W)) {
            angleX += secs;
        }

        if (keys.isDown(GLFW_KEY_S)) {
            angleX -= secs;
        }
        if (keys.isDown(GLFW_KEY_SPACE)) {
            angleY = 0;
            angleX = 0;
        }

        if (keys.isPressed(GLFW_KEY_N)) {
            normals = !normals;
        }
        if(keys.isDown(GLFW_KEY_UP)){
            camera.moveFront(500*secs);
        }
        if(keys.isDown(GLFW_KEY_DOWN)){
            camera.moveFront(-500*secs);
        }
        if(keys.isDown(GLFW_KEY_LEFT)) {
            camera.strafeLeft(10);
        }
        if(keys.isDown(GLFW_KEY_RIGHT)){
            camera.strafeRight(10);
        }
        if(keys.isDown(GLFW_KEY_PAGE_UP)){
            camera.strafeUp(10);
        }
        if(keys.isDown(GLFW_KEY_PAGE_DOWN)){
            camera.strafeDown(10);
        }
        //mostra o grid
        if(keys.isPressed(GLFW_KEY_G)){
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
        }
        //mostra face
        if(keys.isPressed(GLFW_KEY_F)) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        if(keys.isDown(GLFW_KEY_U)){
            scaleMoutain += scaleMoutain > 3 ? 0 : 0.1f;
        }

        if(keys.isDown(GLFW_KEY_I)){
            scaleMoutain -= scaleMoutain < -1 ? 0 : 0.1f;
        }
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Shader shader = mesh.getShader();
        shader.bind()
                .setUniform("uProjection", camera.getProjectionMatrix())
                .setUniform("uView", camera.getViewMatrix())
                .setUniform("uCameraPosition", camera.getPosition());
        light.apply(shader);
        material.apply(shader);
        shader.unbind();
        mesh.setUniform("uWorld", new Matrix4f().rotateY(angleY).rotateX(angleX));
        mesh.setUniform("uScale",scaleMoutain);
        mesh.draw();
    }

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new loadPerlinNoise(), "PerlinNoiseTOP", 1024, 768).show();
    }
}

