package com.imooc.entity;

import javax.persistence.*;

@Entity
@Table(name = "support_address")
public class SupportAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "belong_to")
    private String belongTo;

    @Column(name = "cn_name")
    private String cnName;

    @Column(name = "en_name")
    private String enName;

    private String level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 行政级别定义
     */
    public enum Level{
        CITY("city"), REGION("region");
        private String value;
        Level(String value){
            this.value = value;
        }
        public String getValue(){
            return value;
        }
        public static Level of(String value){
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)){
                    return level;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
