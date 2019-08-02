package com.qingcheng.pojo.goods;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * brand实体类
 */
@Table(name="tb_brand") //JPA注解，将此实体类映射到表tb_brand
public class Brand implements Serializable {
	@Id //JPA注解，指明主键
	private Integer id;//品牌id

	private String name;//品牌名称
	private String image;//品牌图片地址
	private String letter;//品牌名首字母
	private int seq;//排序

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	@Override
	public String toString() {
		return "Brand{" +
				"id=" + id +
				", name='" + name + '\'' +
				", image='" + image + '\'' +
				", letter=" + letter +
				", seq=" + seq +
				'}';
	}
}
