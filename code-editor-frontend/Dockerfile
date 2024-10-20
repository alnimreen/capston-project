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

# Install serve to serve the build directory
RUN npm install -g serve
# Use Nginx to serve the built application
#FROM nginx:alpine

# Copy the built React files to the Nginx server
#COPY --from=build /app/build /usr/share/nginx/html

# Expose the port the app will run on
EXPOSE 3000

CMD ["serve", "-s", "build"]
