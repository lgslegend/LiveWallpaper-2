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

package novumlogic.live.wallpaper.gdx.assets.loaders;

import novumlogic.live.wallpaper.gdx.assets.AssetDescriptor;
import novumlogic.live.wallpaper.gdx.assets.AssetLoaderParameters;
import novumlogic.live.wallpaper.gdx.assets.AssetManager;
import novumlogic.live.wallpaper.gdx.files.FileHandle;
import novumlogic.live.wallpaper.gdx.graphics.Pixmap;
import novumlogic.live.wallpaper.gdx.utils.Array;

/** {@link AssetLoader} for {@link Pixmap} instances. The Pixmap is loaded asynchronously.
 * @author mzechner */
public class PixmapLoader extends AsynchronousAssetLoader<Pixmap, PixmapLoader.PixmapParameter> {
	public PixmapLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	Pixmap pixmap;

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, PixmapParameter parameter) {
		pixmap = null;
		pixmap = new Pixmap(file);
	}

	@Override
	public Pixmap loadSync (AssetManager manager, String fileName, FileHandle file, PixmapParameter parameter) {
		Pixmap pixmap = this.pixmap;
		this.pixmap = null;
		return pixmap;
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, PixmapParameter parameter) {
		return null;
	}

	static public class PixmapParameter extends AssetLoaderParameters<Pixmap> {
	}
}
