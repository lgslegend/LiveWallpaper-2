/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package novumlogic.live.wallpaper.gdx.graphics.glutils;

import novumlogic.live.wallpaper.gdx.Application.ApplicationType;
import novumlogic.live.wallpaper.gdx.Gdx;
import novumlogic.live.wallpaper.gdx.graphics.GL30;
import novumlogic.live.wallpaper.gdx.graphics.Texture;
import novumlogic.live.wallpaper.gdx.graphics.Texture.TextureFilter;
import novumlogic.live.wallpaper.gdx.graphics.Texture.TextureWrap;
import novumlogic.live.wallpaper.gdx.utils.GdxRuntimeException;

/** This is a {@link FrameBuffer} variant backed by a float texture. */
public class FloatFrameBuffer extends FrameBuffer {

	FloatFrameBuffer () {}

	/**
	 * Creates a GLFrameBuffer from the specifications provided by bufferBuilder
	 *
	 * @param bufferBuilder
	 **/
	protected FloatFrameBuffer (GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
		super(bufferBuilder);
	}

	/** Creates a new FrameBuffer with a float backing texture, having the given dimensions and potentially a depth buffer attached.
	 * 
	 * @param width the width of the framebuffer in pixels
	 * @param height the height of the framebuffer in pixels
	 * @param hasDepth whether to attach a depth buffer
	 * @throws GdxRuntimeException in case the FrameBuffer could not be created */
	public FloatFrameBuffer (int width, int height, boolean hasDepth) {
		FloatFrameBufferBuilder bufferBuilder = new FloatFrameBufferBuilder(width, height);
		bufferBuilder.addFloatAttachment(GL30.GL_RGBA32F, GL30.GL_RGBA, GL30.GL_FLOAT, false);
		if (hasDepth) bufferBuilder.addBasicDepthRenderBuffer();
		this.bufferBuilder = bufferBuilder;

		build();
	}

	@Override
	protected Texture createTexture (FrameBufferTextureAttachmentSpec attachmentSpec) {
		FloatTextureData data = new FloatTextureData(
			bufferBuilder.width, bufferBuilder.height,
			attachmentSpec.internalFormat, attachmentSpec.format, attachmentSpec.type,
			attachmentSpec.isGpuOnly
		);
		Texture result = new Texture(data);
		if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet)
			result.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		else
			// no filtering for float textures in OpenGL ES
			result.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		result.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
		return result;
	}

}