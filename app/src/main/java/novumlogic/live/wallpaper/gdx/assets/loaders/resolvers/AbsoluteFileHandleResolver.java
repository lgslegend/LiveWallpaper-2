
package novumlogic.live.wallpaper.gdx.assets.loaders.resolvers;

import novumlogic.live.wallpaper.gdx.Gdx;
import novumlogic.live.wallpaper.gdx.assets.loaders.FileHandleResolver;
import novumlogic.live.wallpaper.gdx.files.FileHandle;

public class AbsoluteFileHandleResolver implements FileHandleResolver {
	@Override
	public FileHandle resolve (String fileName) {
		return Gdx.files.absolute(fileName);
	}
}
