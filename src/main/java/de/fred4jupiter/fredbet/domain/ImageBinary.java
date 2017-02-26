package de.fred4jupiter.fredbet.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "IMAGE_BINARY")
public class ImageBinary {

	@Id
	@Column(name = "KEY")
	private String key;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "IMAGE_BYTES")
	@Lob
	private byte[] imageBinary;

	@Column(name = "IMAGE_GROUP")
	private String imageGroup;

	@Column(name = "THUMB_IMAGE_BYTES")
	@Lob
	private byte[] thumbImageBinary;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	protected ImageBinary() {
		// for hibernate
	}

	public ImageBinary(String key, byte[] imageBinary, String imageGroup, byte[] thumbImageBinary) {
		this.key = key;
		this.imageBinary = imageBinary;
		this.imageGroup = imageGroup;
		this.thumbImageBinary = thumbImageBinary;
	}

	public String getKey() {
		return key;
	}

	public byte[] getImageBinary() {
		return imageBinary;
	}

	public byte[] getThumbImageBinary() {
		return thumbImageBinary;
	}

	public String getImageGroup() {
		return imageGroup;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("key", key);
		builder.append("imageGroup", imageGroup);
		builder.append("image size", imageBinary != null ? imageBinary.length : 0);
		builder.append("image thumb size", thumbImageBinary != null ? thumbImageBinary.length : 0);
		return builder.toString();
	}

}