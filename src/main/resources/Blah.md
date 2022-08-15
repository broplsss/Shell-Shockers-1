 // =============================================================
        //   Generate VAO, VBO, and EBO buffer objects and send to GPU
        // =============================================================

        // Generate the Element Buffer Object to utilize index drawing rathe than overlap drawing
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID); // same thing as with the VBO
        // In order to do the next step, we must create an int buffer of our vertices that is the same length and can be passed into the EBO
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Allocate the Vertex Buffer Object memory on the GPU
        vboID = glGenBuffers();
        // Binds the specified buffer object to the target buffer - GL_ARRAY_BUFFER (for vertex data). All following buffer manipulations will interact with the specified buffer object.
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        // In order to do the next step, we must create a float buffer of our vertices that is the same length and can be passed into the VBO
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        // Now we add the previosly made vertex data to the buffer.
        // Takes in target buffer, data (in the form of a float buffer), and draw type (stream, static, dynamic).
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);



        // Generate the Vertex Array Object to manage a VBO and its configurations. This allows us to simply bind the VAO with the desired configurations to our VBO whenever we want to draw an object.
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Now we need to specify the vertex data attributes (number, stride, offset...)
        // Add the vertex attribute pointers (how many quantitites or floats per category)
        int positionSize = 3; // XYZ
        int colorSize = 0; // RGBA
        int normalVec = 3; // XYZ
        int texSize = 2; // ST
        int vertexSizeBytes = (positionSize + colorSize + normalVec + texSize) * Float.BYTES; // Stride

        // Position attribute
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0); // index (from (layout = n)), size, type, normalized?, stride, offset
        glEnableVertexAttribArray(0); // index to enable ^
                
        // // Color attribute
        // glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES); // offset = previous stride in Bytes
        // glEnableVertexAttribArray(1);

        
        // Texture attribute
        glVertexAttribPointer(1, texSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize + normalVec) * Float.BYTES); // offset = previous stride in Bytes
        glEnableVertexAttribArray(1);
        
        // Normal attribute
        glVertexAttribPointer(2, normalVec, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES); // offset = previous stride in Bytes
        glEnableVertexAttribArray(2);


        // Second VAO for the light source (vertex positions only)
        vao2 = glGenVertexArrays();
        glBindVertexArray(vao2);
        int positionSize2 = 3;

        // Position attribute
        glVertexAttribPointer(0, positionSize2, GL_FLOAT, false, (positionSize2 + normalVec + colorSize + texSize) * Float.BYTES, 0); 
        glEnableVertexAttribArray(0);