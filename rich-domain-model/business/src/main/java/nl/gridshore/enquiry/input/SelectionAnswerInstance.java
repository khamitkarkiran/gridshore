/*
 * Copyright (c) 2009. Gridshore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.gridshore.enquiry.input;

import nl.gridshore.enquiry.def.ChoiceDef;
import nl.gridshore.enquiry.def.MultipleChoiceQuestionDef;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an answer to a question that provides one or more options to choose from.
 *
 * @see nl.gridshore.enquiry.def.MultipleChoiceQuestionDef MultipleChoiceQuestionDef
 * @see nl.gridshore.enquiry.def.SingleChoiceQuestionDef SingleChoiceQuestionDef
 */
@Entity
public class SelectionAnswerInstance extends AnswerInstance {

    @ManyToMany
    @JoinTable(name = "chosen_answers")
    private List<ChoiceDef> choiceDefs = new ArrayList<ChoiceDef>();

    @ManyToOne
    private MultipleChoiceQuestionDef questionDef;

    /**
     * Construct a new answer to the given <code>questionDef</code> using the <code>choices</code>. Each of the choices
     * provided must belong to the provided questionDef.
     *
     * @param questionDef The question to answer
     * @param choices     The choices that make up this answer
     */
    public SelectionAnswerInstance(final MultipleChoiceQuestionDef questionDef, final ChoiceDef... choices) {
        this(questionDef, Arrays.asList(choices));
    }

    /**
     * Construct a new answer to the given <code>questionDef</code> using the <code>choices</code>. Each of the choices
     * provided must belong to the provided questionDef.
     *
     * @param questionDef The question to answer
     * @param choices     The choices that make up this answer
     */
    public SelectionAnswerInstance(final MultipleChoiceQuestionDef questionDef, final List<ChoiceDef> choices) {
        Assert.notNull(choices, "The choices parameter may not be null");
        Assert.notNull(questionDef, "The questionDef parameter may not be null");
        Assert.isTrue(fromSameQuestion(questionDef, choices), "Not all provided choices belong to the same question");

        this.questionDef = questionDef;
        choiceDefs.addAll(choices);
    }

    public List<ChoiceDef> getChoiceDefs() {
        return Collections.unmodifiableList(choiceDefs);
    }

    @Override
    public MultipleChoiceQuestionDef getQuestionDef() {
        return questionDef;
    }

    public boolean isSelected(ChoiceDef choiceDef) {
        for (ChoiceDef def : choiceDefs) {
            if (def.equals(choiceDef)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getAsText() {
        StringBuilder sb = new StringBuilder();
        Iterator<ChoiceDef> iterator = choiceDefs.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().getText());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    // ======================== Helper methods ==============================

    private boolean fromSameQuestion(final MultipleChoiceQuestionDef expectedQuestionDef, final List<ChoiceDef> choices) {
        for (ChoiceDef choice : choices) {
            if (!expectedQuestionDef.sameIdentityAs(choice.getQuestionDef())) {
                return false;
            }
        }
        return true;
    }

    SelectionAnswerInstance() {
        // needed by Hibernate
    }

}
