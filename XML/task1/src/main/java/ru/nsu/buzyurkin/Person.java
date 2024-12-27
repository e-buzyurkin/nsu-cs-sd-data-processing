package ru.nsu.buzyurkin;

import ru.nsu.buzyurkin.exceptions.IllegalRelativeException;
import ru.nsu.buzyurkin.exceptions.PersonMergeException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Person {
    public int id = -1;
    public Gender gender = null;
    public int spouceId = -1;
    public int childrenCheckNumber = -1;
    public int siblingsCheckNumber = -1;
    private String firstName = null;
    private String familyName = null;
    private String spouceName = null;
    private final Set<Integer> parentsIds = new HashSet<>();
    private final Set<String> parentsNames = new HashSet<>(2);
    private final Set<Integer> childrenIds = new HashSet<>();
    private final Set<String> childrenNames = new HashSet<>();
    private final Set<Integer> siblingsIds = new HashSet<>();
    private final Set<String> siblingsNames = new HashSet<>();

    public void tryUpdateId(int newId) throws PersonMergeException {
        if (this.id == -1) {
            this.id = newId;
        } else if (this.id != newId) {
            throw new PersonMergeException(this, "ID", String.valueOf(this.id), String.valueOf(newId));
        }
    }

    public void tryUpdateId(String str) throws PersonMergeException {
        tryUpdateId(Integer.parseInt(str.substring(1)));
    }

    public void tryUpdateFullname(String str) throws PersonMergeException {
        if (str == null) {
            return;
        }

        String[] words = str.trim().split("\\s+");
        if (words.length != 2) {
            return;
        }

        this.tryUpdateFirstName(words[0]);
        this.tryUpdateFamilyName(words[1]);
    }

    public void tryUpdateFirstName(String str) throws PersonMergeException {
        if (this.firstName == null) {
            this.firstName = str;
            return;
        }

        if (!this.firstName.equals(str)) {
            throw new PersonMergeException(this, "First name", this.firstName, str);
        }
    }

    public void tryUpdateFamilyName(String str) throws PersonMergeException {
        if (this.familyName == null) {
            this.familyName = str;
            return;
        }

        if (!this.familyName.equals(str)) {
            throw new PersonMergeException(this, "Family Name", this.familyName, str);
        }
    }

    public void tryUpdateGender(Gender newGenderInfo) throws PersonMergeException {
        if (this.gender == null) {
            this.gender = newGenderInfo;
            return;
        }

        if (!this.gender.equals(newGenderInfo)) {
            throw new PersonMergeException(this, "Gender", this.gender.toString(), newGenderInfo.toString());
        }
    }

    public void tryUpdateSiblingsNumber(int newSiblingsCheckNumber) throws PersonMergeException, IllegalRelativeException {
        if (this.siblingsCheckNumber == -1) {
            if (this.siblingsIds.size() > newSiblingsCheckNumber) {
                throw new IllegalRelativeException(this, "Sibling");
            }

            this.siblingsCheckNumber = newSiblingsCheckNumber;
            return;
        }

        if (this.siblingsCheckNumber != newSiblingsCheckNumber) {
            throw new PersonMergeException(this, "Siblings number", String.valueOf(this.siblingsCheckNumber), String.valueOf(newSiblingsCheckNumber));
        }
    }

    public void tryUpdateChildrenNumber(int childrenNum) throws PersonMergeException, IllegalRelativeException {
        if (this.childrenCheckNumber == -1) {
            if (this.childrenIds.size() > childrenNum) {
                throw new IllegalRelativeException(this, "Child");
            }

            this.childrenCheckNumber = childrenNum;
            return;
        }

        if (this.childrenCheckNumber != childrenNum) {
            throw new PersonMergeException(this, "Siblings number", String.valueOf(this.childrenCheckNumber), String.valueOf(childrenNum));
        }

    }

    public void tryUpdateSpouce(Person spouce) throws PersonMergeException {
        if (spouce.id != -1) {
            if (this.spouceId == -1) {
                this.spouceId = spouce.id;
            } else if (this.spouceId != spouce.id) {
                throw new PersonMergeException(this, "Spouce ID", String.valueOf(this.spouceId), String.valueOf(spouce.id));
            }
        }
        if (spouce.fullname() != null) {
            if (this.spouceName == null) {
                this.spouceName = spouce.fullname();
            } else if (!this.spouceName.equals(spouce.fullname())) {
                throw new PersonMergeException(this, "Spouce ID", String.valueOf(this.spouceId), String.valueOf(spouce.id));
            }
        }
    }

    public void tryUpdateParentList(Person parent) {
        if (parent.id != -1) {
            this.parentsIds.add(parent.id);
        }

        if (parent.fullname() != null) {
            this.parentsNames.add(parent.fullname());
        }
    }

    public void tryUpdateChildList(Person child) {
        if (child.id != -1) {
            this.childrenIds.add(child.id);
        }
        if (child.fullname() != null) {
            this.childrenNames.add(child.fullname());
        }
    }

    public void tryUpdateSiblingList(Person sibling) {
        if (sibling.id != -1) {
            this.siblingsIds.add(sibling.id);
        }

        if (sibling.fullname() != null) {
            this.siblingsNames.add(sibling.fullname());
        }
    }

    public void tryMerge(Person newPerson) throws PersonMergeException, IllegalRelativeException {
        if (newPerson.id != -1) {
            this.tryUpdateId(newPerson.id);

            if (newPerson.spouceId != -1 && newPerson.spouceId == this.id) {
                throw new PersonMergeException(this, newPerson.spouceId);
            }

            for (Integer parentId : newPerson.parentsIds) {
                if (parentId == this.id) {
                    throw new PersonMergeException(this, parentId);
                }
            }

            for (Integer childId : newPerson.childrenIds) {
                if (childId == this.id) {
                    throw new PersonMergeException(this, childId);
                }
            }

            for (Integer siblingId : newPerson.siblingsIds) {
                if (siblingId == this.id) {
                    throw new PersonMergeException(this, siblingId);
                }
            }
        }

        if (newPerson.firstName != null) {
            this.tryUpdateFirstName(newPerson.firstName);
        }

        if (newPerson.familyName != null) {
            this.tryUpdateFamilyName(newPerson.familyName);
        }

        if (newPerson.gender != null) {
            this.tryUpdateGender(newPerson.gender);
        }

        if (newPerson.childrenCheckNumber != -1) {
            this.tryUpdateChildrenNumber(newPerson.childrenCheckNumber);
        }

        if (newPerson.siblingsCheckNumber != -1) {
            this.tryUpdateSiblingsNumber(newPerson.siblingsCheckNumber);
        }

        if (parentsOverflow(parentsIds, newPerson.parentsIds)) {
            throw new IllegalRelativeException(this, "Parent");
        }
        parentsIds.addAll(newPerson.parentsIds);
        parentsNames.addAll(newPerson.parentsNames);

        if (childrenOverflow(childrenIds, newPerson.childrenIds)) {
            throw new IllegalRelativeException(this, "Child");
        }
        childrenIds.addAll(newPerson.childrenIds);
        childrenNames.addAll(newPerson.childrenNames);

        if (siblingOverflow(siblingsIds, newPerson.siblingsIds)) {
            throw new IllegalRelativeException(this, "Sibling");
        }
        siblingsIds.addAll(newPerson.siblingsIds);
        siblingsNames.addAll(newPerson.siblingsNames);

        Person spouce = new Person();
        spouce.tryUpdateId(newPerson.spouceId);
        spouce.tryUpdateFullname(newPerson.spouceName);
        tryUpdateSpouce(spouce);
    }

    public boolean anythingCommonBesidesName(Person newPerson) {
        // if ids are different then its def different people
        if (this.id != -1 && this.id == newPerson.id) {
            return true;
        }
        // if spouces are different then its def different people
        if (this.spouceId != -1 && newPerson.spouceId != -1 && this.spouceId != newPerson.spouceId) {
            return false;
        // if spouces are same then its the same person
        } else if (newPerson.spouceId != -1 && this.spouceId == newPerson.spouceId) {
            return true;
        }
        // same with gender
        if (this.gender != null && newPerson.gender != null && this.gender != newPerson.gender) {
            return false;
        }
        // if they have different number of children, then its def different person
        if (this.childrenCheckNumber != -1 && newPerson.childrenCheckNumber != -1 && this.childrenCheckNumber != newPerson.childrenCheckNumber) {
            return false;
        }
        // same with siblings
        if (this.siblingsCheckNumber != -1 && newPerson.siblingsCheckNumber != -1 && this.siblingsCheckNumber != newPerson.siblingsCheckNumber) {
            return false;
        }

        // if they have same siblings, then its the same person
        Set<Integer> intersection = new HashSet<>(this.siblingsIds);
        intersection.retainAll(newPerson.siblingsIds);
        if (!newPerson.siblingsIds.isEmpty() && !this.siblingsIds.isEmpty() && !intersection.isEmpty()) {
            return true;
        }

        // same with children
        intersection = new HashSet<>(this.childrenIds);
        intersection.retainAll(newPerson.childrenIds);
        if (!newPerson.childrenIds.isEmpty() && !this.siblingsIds.isEmpty() && !intersection.isEmpty()) {
            return true;
        }

        // if they have more siblings than checknumber, then its def different people
        if (this.siblingsIds.size() > newPerson.siblingsCheckNumber
            || newPerson.siblingsIds.size() > this.siblingsCheckNumber) {
            return false;
        }

        // if we couldnt spot the difference, assume they're the same person
        return true;
    }

    private boolean childrenOverflow(Set<Integer> oldList, Set<Integer> newList) {
        if (this.childrenCheckNumber == -1) return false;

        Set<Integer> combination = new HashSet<>(oldList);
        combination.addAll(newList);
        return combination.size() > this.childrenCheckNumber;
    }

    private boolean siblingOverflow(Set<Integer> oldList, Set<Integer> newList) {
        if (this.siblingsCheckNumber == -1) return false;

        Set<Integer> combination = new HashSet<>(oldList);
        combination.addAll(newList);
        return combination.size() > this.siblingsCheckNumber;
    }

    private boolean parentsOverflow(Set<Integer> oldList, Set<Integer> newList) {
        Set<Integer> combination = new HashSet<>(oldList);
        combination.addAll(newList);
        return combination.size() > 2;
    }

    public String fullname() {
        if (firstName == null || familyName == null) {
            return null;
        }

        return firstName + " " + familyName;
    }

    public String toString() {
        if (this.fullname() != null) return "[" + id + "]: " + fullname();
        else return "[" + id + "]";
    }

    public String toStringVerbose() {
        StringBuilder result = new StringBuilder();

        result.append("Person {\n");
        result.append("  fullname: ").append(this.fullname());
        result.append("  id: ").append(id).append(",\n");
        result.append("  gender: ").append(gender).append(",\n");
        result.append("  spouceId: ").append(spouceId).append(",\n");
        result.append("  childrenCheckNumber: ").append(childrenCheckNumber).append(",\n");
        result.append("  siblingsCheckNumber: ").append(siblingsCheckNumber).append(",\n");
        result.append("  firstName: ").append(firstName).append(",\n");
        result.append("  familyName: ").append(familyName).append(",\n");
        result.append("  spouceName: ").append(spouceName).append(",\n");
        result.append("  parentsIds: ").append(parentsIds).append(",\n");
        result.append("  parentsNames: ").append(parentsNames).append(",\n");
        result.append("  childrenIds: ").append(childrenIds).append(",\n");
        result.append("  childrenNames: ").append(childrenNames).append(",\n");
        result.append("  siblingsIds: ").append(siblingsIds).append(",\n");
        result.append("  siblingsNames: ").append(siblingsNames).append("\n");
        result.append("}");

        return result.toString();
    }

}