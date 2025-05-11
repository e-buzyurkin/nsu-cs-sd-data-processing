
CREATE TABLE Person (
    id TEXT PRIMARY KEY,
    name TEXT,
    gender CHAR(1) CHECK (gender IN ('M', 'F'))
);


CREATE TABLE Spouse (
    person_id TEXT PRIMARY KEY,
    spouse_id TEXT NOT NULL,
    CONSTRAINT fk_spouse_person FOREIGN KEY (person_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT fk_spouse_spouse FOREIGN KEY (spouse_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT chk_spouse_diff CHECK (person_id <> spouse_id)
);


CREATE TABLE Parent (
    child_id TEXT NOT NULL,
    parent_id TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('mother', 'father')),
    PRIMARY KEY (child_id, role),
    
    CONSTRAINT fk_parent_child FOREIGN KEY (child_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT fk_parent_parent FOREIGN KEY (parent_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT chk_parent_diff CHECK (child_id <> parent_id)
);


CREATE TABLE Sibling (
    person_id TEXT NOT NULL,
    sibling_id TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('sister', 'brother')),
    PRIMARY KEY (person_id, sibling_id, role),

    CONSTRAINT fk_sibling_person FOREIGN KEY (person_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT fk_sibling_sibling FOREIGN KEY (sibling_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT chk_sibling_diff CHECK (person_id <> sibling_id)
);


CREATE TABLE Child (
    parent_id TEXT NOT NULL,
    child_id TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('daughter', 'son')),
    PRIMARY KEY (parent_id, child_id, role),

    CONSTRAINT fk_child_parent FOREIGN KEY (parent_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT fk_child_child FOREIGN KEY (child_id) REFERENCES Person(id) ON DELETE CASCADE,
    CONSTRAINT chk_child_diff CHECK (parent_id <> child_id)
);

