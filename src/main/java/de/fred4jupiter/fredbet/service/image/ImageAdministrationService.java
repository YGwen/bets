package de.fred4jupiter.fredbet.service.image;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.web.image.ImageCommand;
import de.fred4jupiter.fredbet.web.image.Rotation;

@Service
@Transactional
public class ImageAdministrationService {

	private static final Logger LOG = LoggerFactory.getLogger(ImageAdministrationService.class);

	@Autowired
	private ImageMetaDataRepository imageMetaDataRepository;

	@Autowired
	private ImageGroupRepository imageGroupRepository;

	@Autowired
	private ImageResizingService imageResizingService;

	@Autowired
	private ImageLocationService imageLocationService;

	@Autowired
	private ImageKeyGenerator imageKeyGenerator;

	public void saveImageInDatabase(byte[] binary, String galleryGroup, String description, Rotation rotation) {
		ImageGroup imageGroup = imageGroupRepository.findByName(galleryGroup);

		if (imageGroup == null) {
			imageGroup = new ImageGroup(galleryGroup);
			imageGroupRepository.save(imageGroup);
		}

		byte[] thumbnail = imageResizingService.createThumbnail(binary, rotation);
		byte[] imageByte = imageResizingService.minimizeToDefaultSize(binary, rotation);

		final String key = imageKeyGenerator.generateKey();

		ImageMetaData image = new ImageMetaData(key, imageGroup);
		image.setDescription(description);
		imageMetaDataRepository.save(image);

		imageLocationService.saveImage(key, imageGroup.getName(), imageByte, thumbnail);
	}

	public List<ImageCommand> fetchAllImages() {
		List<ImageMetaData> all = imageMetaDataRepository.findAll();
		if (all.isEmpty()) {
			return Collections.emptyList();
		}

		List<? extends ImageData> allImages = imageLocationService.findAllImages();
		if (allImages.isEmpty()) {
			return Collections.emptyList();
		}

		Map<String, ImageData> binaryMap = allImages.stream()
				.collect(Collectors.toMap(ImageData::getKey, Function.identity()));

		return all.stream()
				.map(imageMetaData -> toImageCommand(imageMetaData, binaryMap.get(imageMetaData.getImageKey())))
				.collect(Collectors.toList());
	}
	
	public void fetchAllImages(ImageCallback imageCallback) {
		List<ImageMetaData> all = imageMetaDataRepository.findAll();
		if (all.isEmpty()) {
			return;
		}

		List<? extends ImageData> allImages = imageLocationService.findAllImages();
		if (allImages.isEmpty()) {
			return;
		}

		Map<String, ImageData> binaryMap = allImages.stream()
				.collect(Collectors.toMap(ImageData::getKey, Function.identity()));

		for (ImageMetaData imageMetaData : all) {
			ImageData imageData = binaryMap.get(imageMetaData.getImageKey());
			imageCallback.doWithImage(imageData.getBinary(), imageMetaData.getImageGroup().getName());
		}
	}

	private ImageCommand toImageCommand(ImageMetaData imageMetaData, ImageData imageData) {
		ImageCommand imageCommand = new ImageCommand();
		imageCommand.setDescription(imageMetaData.getDescription());
		imageCommand.setGalleryGroup(imageMetaData.getImageGroup().getName());
		imageCommand.setImageId(imageMetaData.getId());
		imageCommand.setThumbImageAsBase64(Base64.getEncoder().encodeToString(imageData.getThumbnailBinary()));
		return imageCommand;
	}

	public byte[] loadImageById(Long imageId) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findOne(imageId);
		if (imageMetaData == null) {
			return null;
		}

		ImageData imageData = imageLocationService.getImageDataByKey(imageMetaData.getImageKey());
		return imageData.getBinary();
	}

	public void deleteImageById(Long imageId) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findOne(imageId);
		if (imageMetaData == null) {
			LOG.warn("Could not found image with id: {}", imageId);
			return;
		}
		
		imageMetaDataRepository.delete(imageId);
		imageLocationService.deleteImage(imageMetaData.getImageKey());
	}
	
	@FunctionalInterface
	public interface ImageCallback{
		
		void doWithImage(byte[] binary, String groupName);
	}
}
