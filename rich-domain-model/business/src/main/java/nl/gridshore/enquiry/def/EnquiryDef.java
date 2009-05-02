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

package nl.gridshore.enquiry.def;

import nl.gridshore.rdm.persistence.BaseEntity;
import org.hibernate.annotations.Cascade;
import org.springframework.util.Assert;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The definition of an Enquiry. An enquiry is regarded as an immutable and ordered collection of questions.
 */
@Entity
public class EnquiryDef extends BaseEntity {

    private static final String PATH_SEPARATOR = ".";

    @Basic
    private String title;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enquiry")
    @OrderBy("index")
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<QuestionDef> questions = new ArrayList<QuestionDef>();

    /**
     * Constructs a new Enquiry Definition instance with the given <code>title</code> and <code>questions</code>.
     *
     * @param title     The title of this enquiry
     * @param questions The questions that make up this enquiry. Each of those questions must be root level questions.
     */
    public EnquiryDef(final String title, final List<QuestionDef> questions) {
        this.title = title;
        int i = 0;
        for (QuestionDef question : questions) {
            Assert.isNull(question.getEnquiry());
            question.setEnquiry(this);
            question.setIndex(i++);
            this.questions.add(question);
        }
    }

    /**
     * Get a question using the a path expression. Each part of the path consists of the index of each question or
     * choice option. The indexes are separated from eachother using the <code>.</code> characer.
     * <p/>
     * Example 1.2.3 will select the fourth (3) question of the third choice of the second question in the enquiry.
     *
     * @param path The path to the question
     * @return the question at the given path, or <code>null</code> if no question could be found
     */
    public QuestionDef getQuestionByPath(String path) {
        int[] paths = splitPath(path);
        // the first item will always result in a question
        QuestionDef question = this.questions.get(paths[0]);
        int[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
        if (subPaths.length > 0) {
            return question.getSubQuestionByPath(subPaths);
        }
        return question;
    }

    /**
     * Get the title of this enquiry as provided in the constructor
     *
     * @return the title of this enquiry definition
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the questions in this enquiry. Only returns the root-level questions.
     *
     * @return an immutable list of questions.
     */
    public List<QuestionDef> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    // ======================== Helper methods ==============================

    private int[] splitPath(final String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, PATH_SEPARATOR);
        int[] items = new int[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            items[i++] = (Integer.parseInt(token));
        }
        return items;
    }

    EnquiryDef() {
        // needed by Hibernate
    }

}
