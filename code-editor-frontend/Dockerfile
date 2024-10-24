# Use the official Node.js image as the base image
FROM node:18-alpine as build

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files
COPY package*.json ./

# Install the dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the application
RUN npm run build

# Use the serve package to serve the build
FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Install the serve package globally
RUN npm install -g serve

# Copy the build files from the previous stage
COPY --from=build /app/build ./build

# Expose the port the app will run on
EXPOSE 3000

# Command to run the serve
CMD ["serve", "-s", "build"]
