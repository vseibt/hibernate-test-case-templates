package org.hibernate.bugs.hhh16003;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PEL_MASTER_DATA_FILE")
public class MasterDataFileEntity {

    @Id
    @AttributeOverride(name = "value", column = @Column(name = "id", nullable = false, length = 36))
    private  PrimaryKey id;

    @Embedded
    private MasterDataMetaData metaData;

    @Column(name = "import_finished_at")
    private LocalDateTime importFinishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_import_status", nullable = false)
    private MasterDataImportStatus dataImportStatus;

    protected MasterDataFileEntity() {
    }

    public MasterDataFileEntity(PrimaryKey id, MasterDataMetaData metaData, LocalDateTime importFinishedAt,
            MasterDataImportStatus dataImportStatus) {
        this.id = id;
        this.metaData = metaData;
        this.importFinishedAt = importFinishedAt;
        this.dataImportStatus = dataImportStatus;
    }


    public PrimaryKey getId() {
        return id;
    }
}
