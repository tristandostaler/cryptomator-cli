package org.cryptomator.cli.frontend;

import org.cryptomator.frontend.fuse.mount.EnvironmentVariables;
import org.cryptomator.frontend.fuse.mount.FuseMountException;
import org.cryptomator.frontend.fuse.mount.FuseMountFactory;
import org.cryptomator.frontend.fuse.mount.Mount;
import org.cryptomator.frontend.fuse.mount.Mounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class FuseMount {
	private static final Logger LOG = LoggerFactory.getLogger(FuseMount.class);

	private Path vaultRoot;
	private Path mountPoint;
	private Mount mnt;
	private String mountFlags;

	public FuseMount(Path vaultRoot, Path mountPoint, String mountFlags) {
		this.vaultRoot = vaultRoot;
		this.mountPoint = mountPoint;
		this.mountFlags = mountFlags;
		this.mnt = null;
	}

	public boolean mount() {
		if (mnt != null) {
			LOG.info("Already mounted to {}", mountPoint);
			return false;
		}

		try {
			Mounter mounter = FuseMountFactory.getMounter();

			EnvironmentVariables envVars ;
			if (mountFlags != null) {
				ArrayList<String> defaultMountFlags = new ArrayList<String>(Arrays.asList(mounter.defaultMountFlags()));
				for (String it : mountFlags.split(",")) {
					defaultMountFlags.add("-o"+it.replace(' ','='));
				}
				String[] newMountFlags = defaultMountFlags.toArray(new String[defaultMountFlags.size()]);
				envVars = EnvironmentVariables.create()
						.withFlags(newMountFlags)
						.withFileNameTranscoder(mounter.defaultFileNameTranscoder())
						.withMountPoint(mountPoint).build();
			} else {
				envVars = EnvironmentVariables.create()
						.withFlags(mounter.defaultMountFlags())
						.withFileNameTranscoder(mounter.defaultFileNameTranscoder())
						.withMountPoint(mountPoint).build();
			}
			mnt = mounter.mount(vaultRoot, envVars);
			LOG.info("Mounted to {}", mountPoint);
		} catch (FuseMountException e) {
			LOG.error("Can't mount: {}, error: {}", mountPoint, e.getMessage());
			return false;
		}
		return true;
	}

	public void unmount() {
		try {
			mnt.unmount();
			LOG.info("Unmounted {}", mountPoint);
		} catch (FuseMountException e) {
			LOG.error("Can't unmount gracefully: {}. Force unmount.", e.getMessage());
			forceUnmount();
		}
	}

	private void forceUnmount() {
		try {
			mnt.unmountForced();
			LOG.info("Unmounted {}", mountPoint);
		} catch (FuseMountException e) {
			LOG.error("Force unmount failed: {}", e.getMessage());
		}
	}
}
